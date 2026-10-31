const fs = require('node:fs');

function diagnostics(log) {
    if (!log.includes('BUILD SUCCESSFUL')) throw Error('Only a successful full compilation can be checked');
    for (const task of [':compileJava', ':gtnllib:compileJava']) {
        if (!log.split(/\r?\n/).includes('> Task ' + task)) {
            throw Error(task + ' must execute; pass --rerun to both compileJava tasks');
        }
    }
    const counts = {};
    for (const line of log.split(/\r?\n/)) {
        const warning = /^(.*\.java):\d+:.*\[(deprecation|removal)\]/.exec(line);
        if (!warning) continue;
        const path = warning[1].replaceAll('\\', '/');
        const source = /(?:^|\/)(gtnllib\/src\/main\/java\/.*\.java)$/.exec(path)
            || /(?:^|\/)(src\/main\/java\/.*\.java)$/.exec(path);
        if (!source) throw Error('Unrecognized warning source: ' + path);
        const key = source[1] + '\t' + warning[2];
        counts[key] = (counts[key] || 0) + 1;
    }
    return Object.fromEntries(Object.entries(counts).sort(([a], [b]) => a.localeCompare(b, 'en')));
}

function increased(actual, baseline) {
    return Object.entries(actual).filter(([path, count]) => count > (baseline[path] || 0));
}

if (require.main === module) {
    const [logPath, baselinePath, mode] = process.argv.slice(2);
    if (!logPath || !baselinePath || (mode && mode !== '--record')) throw Error('Usage: node check-deprecations.cjs <build log> <baseline.json> [--record]');
    const actual = diagnostics(fs.readFileSync(logPath, 'utf8'));
    if (mode === '--record') fs.writeFileSync(baselinePath, JSON.stringify(actual, null, 2) + '\n');
    else {
        const additions = increased(actual, JSON.parse(fs.readFileSync(baselinePath, 'utf8')));
        if (additions.length) throw Error('New deprecated API usage; fix it or review the per-file baseline: ' + JSON.stringify(additions));
    }
    console.log('Deprecation baseline checked: ' + Object.values(actual).reduce((a, b) => a + b, 0) + ' warnings in ' + Object.keys(actual).length + ' source/kind entries');
}

module.exports = { diagnostics, increased };
