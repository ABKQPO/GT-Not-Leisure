const fs = require('node:fs');

function missingResources(log, report) {
    if (!log.includes('GTNL_CLIENT_SMOKE_READY') || !log.includes('BUILD SUCCESSFUL')) throw Error('Client did not reach the main menu and exit successfully');
    if (/GTNL_CLIENT_SMOKE_FAILED|Crash report saved|BUILD FAILED/.test(log)) throw Error('Client startup failed');
    for (const atlas of ['blocks', 'items']) {
        const match = new RegExp('^checked ' + atlas + ' ([1-9][0-9]*)$', 'm').exec(report.replaceAll('\r', ''));
        if (!match) throw Error('No registered mod sprites were checked in ' + atlas);
    }
    const missing = new Set(report.split(/\r?\n/).filter(line => line.startsWith('atlas ')));
    const messages = /(?:unable to load|failed to load texture:)\s*((?:sciencenotleisure|gtnllib|reavaritia):[^\s,]+)/gi;
    for (const match of log.matchAll(messages)) missing.add('log ' + match[1]);
    return [...missing].sort();
}

if (require.main === module) {
    const [logPath, reportPath, baselinePath] = process.argv.slice(2);
    if (!logPath || !reportPath || !baselinePath) throw Error('Usage: node check-client-resources.cjs <client log> <atlas report> <baseline>');
    const missing = missingResources(fs.readFileSync(logPath, 'utf8'), fs.readFileSync(reportPath, 'utf8'));
    const allowed = new Set(fs.readFileSync(baselinePath, 'utf8').split(/\r?\n/).filter(line => line && !line.startsWith('#')));
    const additions = missing.filter(line => !allowed.has(line));
    if (additions.length) throw Error('New missing client resources:\n' + additions.join('\n'));
    console.log('Client startup and missing-texture baseline checked: ' + missing.length + ' known missing resources');
}

module.exports = { missingResources };
