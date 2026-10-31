const { test } = require('node:test');
const assert = require('node:assert/strict');
const { expectedCases, validateReport, junitReport, validateEnvironment } = require('./runtime-report.cjs');
const expected = ['world.steam.restart-read'];
const good = () => ({
    schemaVersion: 1,
    runId: 'r1',
    phase: 'restart',
    state: 'PASS',
    results: [{ id: expected[0], scope: 'world-disk-persistence', status: 'PASS', ticks: 1, detail: '' }],
});
test('catalog additions become required scenarios', () => {
    assert.deepEqual(expectedCases(' A(1),\n B(2);\n', 'server'), [
        'machine.a.persistence',
        'machine.b.persistence',
        'steam.hatch.simulation-payment-reload',
        'production.large_steam_furnace.structure-and-smelting',
        'world.steam.restart-write',
    ]);
    assert.throws(() => expectedCases('', 'server'));
});
test('rejects empty, stale, duplicate, missing and unfinished evidence', () => {
    assert.deepEqual(validateReport(good(), expected, 'r1', 'restart'), { passed: 1, skipped: 0 });
    for (const change of [
        (r) => (r.results = []),
        (r) => (r.runId = 'old'),
        (r) => (r.phase = 'server'),
        (r) => (r.state = 'RUNNING'),
        (r) => r.results.push(r.results[0]),
        (r) => (r.results[0].ticks = 0),
    ]) {
        const report = good();
        change(report);
        assert.throws(() => validateReport(report, expected, 'r1', 'restart'));
    }
    for (const status of ['FAIL', 'TIMEOUT', 'SKIP', 'NOT_RUN', 'RUNNING']) {
        const report = good();
        report.results[0].status = status;
        assert.throws(() => validateReport(report, expected, 'r1', 'restart'));
    }
});
test('failure reports retain escaped details and do not count NOT_RUN as success', () => {
    const report = good();
    report.results[0].status = 'NOT_RUN';
    report.results[0].detail = 'missing <world> & "owner"';
    assert.match(junitReport(report), /failures="1"/);
    assert.match(junitReport(report), /missing &lt;world&gt; &amp; &quot;owner&quot;/);
});
test('expands reserved cable and pipe families even with inline comments', () => {
    const cases = expectedCases(
        ' STAR_GATE_WIRE(22506), // reserved range\n BLUE_ALLOY_PIPE(22519); // five variants\n',
        'client',
    );
    assert.equal(cases.length, 17);
    assert.ok(cases.includes('machine.star_gate_wire.variant_11.gui'));
    assert.ok(cases.includes('machine.blue_alloy_pipe.variant_4.gui'));
});
test('GUI success requires actual rendered window evidence and cannot skip a required controller', () => {
    const report = good();
    report.phase = 'client';
    report.results[0].id = 'machine.large_steam_furnace.gui';
    const cases = [report.results[0].id];
    assert.throws(() => validateReport(report, cases, 'r1', 'client'), /frame evidence/);
    Object.assign(report.results[0], {
        windowId: 1,
        screenClass: 'native.GuiContainer',
        screenshot: 'screenshots/frame.png',
    });
    assert.throws(() => validateReport(report, cases, 'r1', 'client'), /interaction evidence/);
    Object.assign(report.results[0], {
        checks: [
            'steam.overclock.client-server',
            'steam.overclock.range',
            'steam.overclock.restore',
            'steam.overclock.popup-lifecycle',
        ],
        additionalScreenshots: ['screenshots/popup.png'],
    });
    assert.deepEqual(validateReport(report, cases, 'r1', 'client'), { passed: 1, skipped: 0 });
    Object.assign(report.results[0], {
        status: 'SKIP',
        detail: 'This registered machine exposes no server container on activation',
    });
    assert.throws(() => validateReport(report, cases, 'r1', 'client'), /Unsuccessful scenario/);
});

test('only explicit reviewed capabilities may skip a native GUI', () => {
    const { noContainer, schemaVersion } = require('./gui-expectations.json');
    const source = require('node:fs').readFileSync(
        require('node:path').join(
            __dirname,
            '../../src/main/java/com/science/gtnl/utils/enums/GTNLMachineID.java',
        ),
        'utf8',
    );
    const catalog = expectedCases(source, 'client');
    assert.equal(schemaVersion, 1);
    for (const [id, reason] of Object.entries(noContainer)) {
        assert.ok(catalog.includes(id), 'Unknown exception ' + id);
        assert.ok(reason.trim().length > 0);
    }
    const report = good();
    report.phase = 'client';
    const result = report.results[0];
    result.id = 'machine.steamgate.gui';
    result.status = 'SKIP';
    result.detail = noContainer[result.id];
    assert.deepEqual(validateReport(report, [result.id], 'r1', 'client'), { passed: 0, skipped: 1 });
    result.detail = 'arbitrary failure';
    assert.throws(() => validateReport(report, [result.id], 'r1', 'client'), /Unsuccessful/);
    result.status = 'PASS';
    assert.throws(() => validateReport(report, [result.id], 'r1', 'client'), /Obsolete/);
    result.id = 'machine.dual_input_hatch_max.gui';
    Object.assign(result, { windowId: 1, screenClass: 'native.GuiContainer', screenshot: 'frame.png' });
    assert.throws(() => validateReport(report, [result.id], 'r1', 'client'), /scrolling evidence/);
});
test('world restart must use the same actual host, library and helper code', () => {
    const code = Object.fromEntries(
        ['host', 'library', 'testMod'].map((name) => [
            name,
            { location: name + '.jar', files: 1, sha256: 'a'.repeat(64) },
        ]),
    );
    const first = {
        schemaVersion: 1,
        runId: 'r1',
        phase: 'server',
        code,
        mods: { sciencenotleisure: '1', gtnllib: '1', gtnlruntimetests: '1' },
    };
    validateEnvironment(first, 'r1', 'server');
    const restarted = structuredClone(first);
    restarted.phase = 'restart';
    validateEnvironment(restarted, 'r1', 'restart', first);
    restarted.code.host.sha256 = 'b'.repeat(64);
    assert.throws(() => validateEnvironment(restarted, 'r1', 'restart', first), /changed between/);
    restarted.code.host.files = 0;
    assert.throws(() => validateEnvironment(restarted, 'r1', 'restart'), /identity/);
    assert.throws(() => validateEnvironment(first, 'other', 'server'), /Stale/);
});
