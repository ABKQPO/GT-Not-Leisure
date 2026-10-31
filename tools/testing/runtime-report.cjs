const { noContainer } = require('./gui-expectations.json');

function expectedCases(enumSource, phase) {
    if (phase === 'restart') return ['world.steam.restart-read'];
    if (!['server', 'client'].includes(phase)) throw Error('Unknown phase: ' + phase);
    const machines = [...enumSource.matchAll(/^\s*(\w+)\((\d+)\)[,;]?\s*(?:\/\/[^\n]*)?$/gm)].flatMap((m) => {
        const count =
            m[1] === 'STAR_GATE_WIRE'
                ? 12
                : ['BLUE_ALLOY_PIPE', 'COMPRESSED_STEAM_PIPE', 'STRONZE_PIPE', 'BREEL_PIPE'].includes(m[1])
                  ? 5
                  : 1;
        return Array.from(
            { length: count },
            (_, variant) =>
                'machine.' + m[1].toLowerCase() + (variant ? '.variant_' + variant : '') + '.persistence',
        );
    });
    if (!machines.length || new Set(machines).size !== machines.length)
        throw Error('Invalid machine catalog');
    return phase === 'client'
        ? machines.map((id) => id.replace('.persistence', '.gui'))
        : [
              ...machines,
              'steam.hatch.simulation-payment-reload',
              'production.large_steam_furnace.structure-and-smelting',
              'world.steam.restart-write',
          ];
}

function validateReport(report, expected, runId, phase) {
    if (report.schemaVersion !== 1 || report.runId !== runId || report.phase !== phase)
        throw Error('Stale report or wrong schema/run/phase');
    if (report.state !== 'PASS') throw Error('Runtime scenarios did not pass: ' + report.state);
    if (!Array.isArray(report.results) || !report.results.length) throw Error('Runtime report is empty');
    const ids = report.results.map((r) => r.id);
    if (
        new Set(ids).size !== ids.length ||
        ids.length !== expected.length ||
        expected.some((id) => !ids.includes(id))
    )
        throw Error('Missing, duplicate or unexpected runtime scenarios');
    for (const r of report.results) {
        if (r.status === 'PASS') {
            if (phase === 'client' && Object.hasOwn(noContainer, r.id))
                throw Error('Obsolete no-container exception: ' + r.id);
            if (!Number.isInteger(r.ticks) || r.ticks < 1)
                throw Error('Passed scenario was not ticked: ' + r.id);
            if (
                phase === 'client' &&
                (!r.screenshot || !r.screenClass || !Number.isInteger(r.windowId) || r.windowId <= 0)
            )
                throw Error('GUI pass without actual window/frame evidence: ' + r.id);
            if (phase === 'client' && r.id === 'machine.large_steam_furnace.gui') {
                const required = [
                    'steam.overclock.client-server',
                    'steam.overclock.range',
                    'steam.overclock.restore',
                    'steam.overclock.popup-lifecycle',
                ];
                if (
                    !Array.isArray(r.checks) ||
                    required.some((check) => !r.checks.includes(check)) ||
                    !r.additionalScreenshots?.length
                )
                    throw Error('Steam GUI pass without popup and interaction evidence');
            }
            if (
                phase === 'client' &&
                /^machine\.(?:humongous_)?dual_input_hatch_(?:uxv|max)\.gui$/.test(r.id)
            ) {
                if (
                    !['hatch.scroll.all-slots', 'hatch.scroll.last-row'].every((check) =>
                        r.checks?.includes(check),
                    ) ||
                    !r.additionalScreenshots?.length
                )
                    throw Error('Hatch GUI pass without scrolling evidence: ' + r.id);
            }
        } else if (
            r.status === 'SKIP' &&
            (/^machine\.(?:blood_soul_sacrificial_array|loot_bag_redemption|star_gate_wire\.variant_(?:[6-9]|1[01]))\.(?:persistence|gui)$/.test(
                r.id,
            ) ||
                (phase === 'client' && Object.hasOwn(noContainer, r.id) && r.detail === noContainer[r.id])) &&
            r.detail
        )
            continue;
        else throw Error('Unsuccessful scenario: ' + r.id + ' ' + r.status);
    }
    if (phase === 'client')
        for (const id of [
            'machine.large_steam_furnace.gui',
            'machine.assembler_matrix.gui',
            'machine.big_steam_input_hatch.gui',
        ]) {
            if (expected.includes(id) && !report.results.some((r) => r.id === id && r.status === 'PASS'))
                throw Error('Required GUI was not exercised: ' + id);
        }
    return {
        passed: report.results.filter((r) => r.status === 'PASS').length,
        skipped: report.results.filter((r) => r.status === 'SKIP').length,
    };
}

function xml(value) {
    return String(value)
        .replaceAll('&', '&amp;')
        .replaceAll('<', '&lt;')
        .replaceAll('>', '&gt;')
        .replaceAll('"', '&quot;');
}

function validateEnvironment(environment, runId, phase, previous) {
    if (environment.schemaVersion !== 1 || environment.runId !== runId || environment.phase !== phase)
        throw Error('Stale runtime environment evidence');
    for (const name of ['host', 'library', 'testMod']) {
        const code = environment.code?.[name];
        if (
            !code ||
            !/^[a-f0-9]{64}$/.test(code.sha256) ||
            !Number.isInteger(code.files) ||
            code.files < 1 ||
            !code.location
        )
            throw Error('Missing loaded-code identity: ' + name);
        if (previous && previous.code?.[name]?.sha256 !== code.sha256)
            throw Error('Loaded code changed between world save and restart: ' + name);
    }
    for (const id of ['sciencenotleisure', 'gtnllib', 'gtnlruntimetests'])
        if (!environment.mods?.[id]) throw Error('Required mod absent from runtime identity: ' + id);
    return environment;
}
function junitReport(report) {
    const cases = report.results || [];
    const failed = cases.filter((r) => !['PASS', 'SKIP'].includes(r.status)).length;
    return (
        '<?xml version="1.0" encoding="UTF-8"?>\n<testsuite name="GTNL runtime ' +
        xml(report.phase) +
        '" tests="' +
        cases.length +
        '" failures="' +
        failed +
        '" skipped="' +
        cases.filter((r) => r.status === 'SKIP').length +
        '">\n' +
        cases
            .map((r) => {
                let content = '';
                if (r.status === 'SKIP') content = '<skipped message="' + xml(r.detail) + '"/>';
                else if (r.status !== 'PASS')
                    content =
                        '<failure message="' +
                        xml(r.status) +
                        '">' +
                        xml(r.detail || 'Scenario did not finish') +
                        '</failure>';
                return (
                    '<testcase classname="' +
                    xml(r.scope) +
                    '" name="' +
                    xml(r.id) +
                    '">' +
                    content +
                    '</testcase>'
                );
            })
            .join('\n') +
        '\n</testsuite>\n'
    );
}

module.exports = { expectedCases, validateReport, junitReport, validateEnvironment };
