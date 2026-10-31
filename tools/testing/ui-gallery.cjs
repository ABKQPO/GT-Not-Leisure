const fs = require('node:fs');
const path = require('node:path');
const crypto = require('node:crypto');
const escape = text => String(text || '').replaceAll('&','&amp;').replaceAll('<','&lt;').replaceAll('>','&gt;').replaceAll('"','&quot;');
function writeGallery(directory, report) {
    const manifest = [];
    const cards = report.results.map(result => {
        let image = '';
        if (result.screenshot) {
            const file = path.resolve(directory,result.screenshot);
            if (!file.startsWith(path.resolve(directory)+path.sep) || fs.realpathSync(file)!==file) throw Error('Screenshot path escapes the run');
            const bytes = fs.readFileSync(file);
            if (bytes.subarray(0,8).toString('hex') !== '89504e470d0a1a0a') throw Error('Screenshot is not PNG: '+result.id);
            manifest.push({id:result.id,path:result.screenshot,bytes:bytes.length,sha256:crypto.createHash('sha256').update(bytes).digest('hex')});
            image='<a href="'+escape(result.screenshot)+'"><img loading="lazy" src="'+escape(result.screenshot)+'" alt="'+escape(result.id)+'"></a>';
        } else if(result.status==='PASS') throw Error('No screenshot for passed GUI: '+result.id);
        return '<article><h2>'+escape(result.id)+'</h2><strong>'+escape(result.status)+'</strong><p>'+escape(result.detail)+'</p>'+image+'</article>';
    });
    fs.writeFileSync(path.join(directory,'gallery.html'),'<!doctype html><meta charset="utf-8"><title>GTNL GUI regression '+escape(report.runId)+'</title><style>body{font:16px system-ui;margin:24px;background:#f2f4f7}main{display:grid;grid-template-columns:repeat(auto-fit,minmax(340px,1fr));gap:16px}article{background:white;padding:16px;border-radius:8px}h2{font-size:16px;overflow-wrap:anywhere}img{width:100%}p{font-size:13px}</style><h1>GTNL GUI regression</h1><p>Run '+escape(report.runId)+' · '+escape(report.state)+' · actual rendered frames and native container open/close; unformed machine fixtures; no physical-input or remote-TCP claim.</p><main>'+cards.join('\n')+'</main>');
    fs.writeFileSync(path.join(directory,'screenshots.json'),JSON.stringify(manifest,null,2)+'\n');
    return manifest;
}
module.exports={writeGallery};
