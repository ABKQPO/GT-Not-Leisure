const fs = require('node:fs');
const path = require('node:path');
const { execFileSync } = require('node:child_process');

// The CI runner belongs to this job; inspect only its Minecraft launcher, never Gradle workers.
setTimeout(() => {
    const jcmd = path.join(process.env.JAVA_HOME, 'bin', 'jcmd');
    try {
        const processes = execFileSync(jcmd, ['-l'], { encoding: 'utf8', timeout: 15000 });
        const minecraft = processes.split('\n').find(line => /^\d+\s+GradleStart(?:\s|$)/.test(line));
        if (!minecraft) throw Error('Minecraft launcher not found:\n' + processes);
        const pid = minecraft.split(/\s+/, 1)[0];
        const threads = execFileSync(jcmd, [pid, 'Thread.print'], { encoding: 'utf8', timeout: 15000, maxBuffer: 8 * 1024 * 1024 });
        fs.writeFileSync('build/client-threads.txt', threads);
    } catch (error) {
        fs.writeFileSync('build/client-threads.txt', String(error));
    }
}, 300000);
