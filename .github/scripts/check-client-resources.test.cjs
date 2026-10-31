const test = require('node:test');
const assert = require('node:assert/strict');
const { missingResources } = require('./check-client-resources.cjs');
const log = 'GTNL_CLIENT_SMOKE_READY\nBUILD SUCCESSFUL';
const report = 'checked blocks 10\nchecked items 20\n';

test('startup, successful exit and both registered atlases are mandatory', () => {
    assert.deepEqual(missingResources(log, report), []);
    assert.throws(() => missingResources('BUILD SUCCESSFUL', report));
    assert.throws(() => missingResources(log + '\nCrash report saved', report));
    assert.throws(() => missingResources(log, report.replace('items 20', 'items 0')));
});

test('atlas fallback and missing texture logs are both checked, without unrelated mod noise', () => {
    assert.deepEqual(missingResources(log + '\nUsing missing texture, unable to load sciencenotleisure:textures/items/foo.png' +
        '\nFailed to load texture: gtnllib:textures/gui/foo.png\nunable to load anothermod:foo.png',
        report + 'atlas blocks reavaritia:foo\n'), [
        'atlas blocks reavaritia:foo', 'log gtnllib:textures/gui/foo.png', 'log sciencenotleisure:textures/items/foo.png'
    ]);
});
