#!/usr/bin/env node
const fs = require('node:fs');
const path = require('node:path');
const crypto = require('node:crypto');
const {spawn, execFileSync} = require('node:child_process');
const {expectedCases, validateReport, junitReport} = require('./runtime-report.cjs');
const options=process.argv.slice(2);
let clientMode=false, selectedCase='';
while(options.length){const arg=options.shift();if(arg==='--client')clientMode=true;else if(arg==='--server')clientMode=false;else if(arg==='--case' && options.length)selectedCase=options.shift();else throw Error('Usage: node tools/testing/run-runtime-tests.cjs [--server|--client] [--case scenario.id]');}
if(selectedCase && !/^[a-z0-9_.-]+$/.test(selectedCase))throw Error('Invalid case ID');
const repo = path.resolve(__dirname, '../..');
const root = path.join(repo, 'build/runtime-tests');
const runId = new Date().toISOString().replace(/[^0-9]/g, '') + '-' + crypto.randomBytes(3).toString('hex');
const directory = path.join(root, runId);
const game = path.join(directory, 'game');
const enumSource = fs.readFileSync(path.join(repo, 'src/main/java/com/science/gtnl/utils/enums/GTNLMachineID.java'), 'utf8');
const sha256 = file => crypto.createHash('sha256').update(fs.readFileSync(file)).digest('hex');

function writeJson(name, value) { fs.writeFileSync(path.join(directory,name), JSON.stringify(value,null,2) + '\n'); }
async function launch(phase) {
    const reportPath = path.join(directory, phase + '.json');
    const stdoutPath = path.join(directory, phase + '.log');
    const expected = expectedCases(enumSource, phase).filter(id=>!selectedCase || phase==='restart' || id===selectedCase);
    if(!expected.length)throw Error('No scenario matches: '+selectedCase);
    writeJson(phase + '-plan.json', {runId,phase,expected});
    const args = ['--no-daemon', '--console=plain', '-Dorg.gradle.jvmargs=-Xmx2G -XX:MaxMetaspaceSize=512m -Dfile.encoding=UTF-8', '-Pgtnl.runtimeDir=' + game, phase === 'client' ? ':runClient25' : ':runServer25'];
    if (process.env.GTNL_TEST_OFFLINE === '1') args.unshift('--offline');
    const windows = process.platform === 'win32';
    const log = fs.openSync(stdoutPath, 'wx');
    const child = spawn(windows ? 'cmd.exe' : './gradlew', windows ? ['/d','/c','gradlew.bat', ...args] : args, {
        cwd:repo, windowsHide:true, detached:!windows, stdio:['pipe','pipe','pipe'],
        env:{...process.env, GTNL_RUNTIME_TESTS:'1', GTNL_TEST_RUN_ID:runId, GTNL_TEST_PHASE:phase, GTNL_TEST_REPORT:reportPath, GTNL_TEST_CASE:phase==='restart'?'':selectedCase}
    });
    const receipt = {phase, runId, pid:child.pid, startedAt:new Date().toISOString(), status:'RUNNING', forcedStop:false, expectedScenarios:expected.length};
    writeJson(phase + '-receipt.json',receipt);
    console.log('Runtime phase ' + phase + ', PID ' + child.pid + ', log ' + stdoutPath);
    let tail='', done=false;
    const output = data => {fs.writeSync(log,data);tail=(tail+data.toString()).slice(-16000);if(tail.includes('Done ('))done=true;};
    child.stdout.on('data',output); child.stderr.on('data',output); child.stdin.on('error',()=>{});
    const timer = setTimeout(()=>{
        receipt.forcedStop=true;
        if(windows)spawn('taskkill.exe',['/PID',String(child.pid),'/T','/F'],{windowsHide:true});
        else {try{process.kill(-child.pid,'SIGKILL');}catch{}}
    }, (phase === 'client' ? 40 : 20)*60*1000);
    let code;
    try {code = await new Promise((resolve,reject)=>{child.once('error',reject);child.once('close',resolve);});}
    finally {clearTimeout(timer);fs.closeSync(log);}
    receipt.exitCode=code;receipt.finishedAt=new Date().toISOString();receipt.doneSeen=done;
    try {
        if(!fs.existsSync(reportPath)) throw Error('Runtime test mod did not produce a report');
        const report=JSON.parse(fs.readFileSync(reportPath,'utf8'));
        fs.writeFileSync(path.join(directory,phase+'.xml'),junitReport(report));
        receipt.counts=validateReport(report,expected,runId,phase);
        if (phase === 'client') receipt.screenshots = require('./ui-gallery.cjs').writeGallery(directory, report);
        if(code!==0 || receipt.forcedStop || (phase !== 'client' && !done) || !tail.includes('BUILD SUCCESSFUL') || !tail.includes('GTNL_RUNTIME_COMPLETE')) throw Error('Game did not complete and stop normally');
        receipt.reportSha256=sha256(reportPath);receipt.status='PASS';
        console.log(phase + ': ' + receipt.counts.passed + ' passed, ' + receipt.counts.skipped + ' explicitly skipped');
    } catch(error) {receipt.status='FAIL';receipt.error=String(error);throw error;}
    finally {writeJson(phase+'-receipt.json',receipt);}
}

async function main() {
    fs.mkdirSync(root,{recursive:true});
    if(fs.realpathSync(root)!==path.resolve(root))throw Error('Runtime root must not be redirected');
    fs.mkdirSync(directory);fs.mkdirSync(game);
    fs.writeFileSync(path.join(game,'.gtnl-test-workspace'),runId+'\n',{flag:'wx'});
    fs.writeFileSync(path.join(game,'eula.txt'),'eula=true\n');
    fs.writeFileSync(path.join(game,'server.properties'),'online-mode=true\nserver-ip=127.0.0.1\nserver-port=0\nlevel-name=GTNLTestWorld\nlevel-type=FLAT\ngenerate-structures=false\nlevel-seed=663\n');
    const revision=execFileSync('git',['rev-parse','HEAD'],{cwd:repo,encoding:'utf8'}).trim();
    writeJson('run.json',{runId,revision,runnerSha256:sha256(__filename),catalogSha256:crypto.createHash('sha256').update(enumSource).digest('hex'),executionScope:'development runtime; generated world only',physicalInput:'NOT_RUN',remoteTcp:'NOT_RUN',status:'RUNNING'});
    console.log('Fresh runtime test workspace: ' + directory);
    if (clientMode) await launch('client');
    else { await launch('server'); if(!selectedCase || selectedCase==='world.steam.restart-write')await launch('restart'); }
    const summary=JSON.parse(fs.readFileSync(path.join(directory,'run.json'),'utf8'));summary.status='PASS';summary.finishedAt=new Date().toISOString();writeJson('run.json',summary);
}
main().catch(error=>{console.error(error.stack);if(fs.existsSync(path.join(directory,'run.json'))){const summary=JSON.parse(fs.readFileSync(path.join(directory,'run.json'),'utf8'));summary.status='FAIL';summary.error=String(error);writeJson('run.json',summary);}process.exitCode=1;});
