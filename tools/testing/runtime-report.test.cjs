const {test} = require('node:test');
const assert = require('node:assert/strict');
const {expectedCases, validateReport, junitReport} = require('./runtime-report.cjs');
const expected = ['world.steam.restart-read'];
const good = () => ({schemaVersion:1, runId:'r1', phase:'restart', state:'PASS', results:[{id:expected[0], scope:'world-disk-persistence', status:'PASS', ticks:1, detail:''}]});
test('catalog additions become required scenarios', () => {
    assert.deepEqual(expectedCases(' A(1),\n B(2);\n', 'server'), ['machine.a.persistence','machine.b.persistence','steam.hatch.simulation-payment-reload','world.steam.restart-write']);
    assert.throws(()=>expectedCases('', 'server'));
});
test('rejects empty, stale, duplicate, missing and unfinished evidence', () => {
    assert.deepEqual(validateReport(good(), expected, 'r1', 'restart'), {passed:1,skipped:0});
    for (const change of [r=>r.results=[], r=>r.runId='old', r=>r.phase='server', r=>r.state='RUNNING', r=>r.results.push(r.results[0]), r=>r.results[0].ticks=0]) {
        const report=good(); change(report); assert.throws(()=>validateReport(report,expected,'r1','restart'));
    }
    for (const status of ['FAIL','TIMEOUT','SKIP','NOT_RUN','RUNNING']) {const report=good();report.results[0].status=status;assert.throws(()=>validateReport(report,expected,'r1','restart'));}
});
test('failure reports retain escaped details and do not count NOT_RUN as success', () => {
    const report=good(); report.results[0].status='NOT_RUN'; report.results[0].detail='missing <world> & "owner"';
    assert.match(junitReport(report), /failures="1"/);
    assert.match(junitReport(report), /missing &lt;world&gt; &amp; &quot;owner&quot;/);
});
