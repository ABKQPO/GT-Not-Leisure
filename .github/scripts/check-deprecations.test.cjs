const test = require('node:test');
const assert = require('node:assert/strict');
const { diagnostics, increased } = require('./check-deprecations.cjs');
const executed = '> Task :compileJava\n> Task :gtnllib:compileJava\nBUILD SUCCESSFUL\n';

test('counts source warnings independently of OS, language and line number', () => {
    const warnings = diagnostics(executed +
        'C:\\repo\\src\\main\\java\\com\\example\\Host.java:10: 警告: [deprecation] old method\n' +
        '/home/repo/gtnllib/src/main/java/com/example/Lib.java:42: warning: [removal] old method\n');
    assert.equal(warnings['src/main/java/com/example/Host.java\tdeprecation'], 1);
    assert.equal(warnings['gtnllib/src/main/java/com/example/Lib.java\tremoval'], 1);
});

test('cached compilation and failed builds cannot look like zero warnings', () => {
    assert.throws(() => diagnostics(executed.replace('> Task :compileJava', '> Task :compileJava UP-TO-DATE')));
    assert.throws(() => diagnostics(executed.replace('BUILD SUCCESSFUL', 'BUILD FAILED')));
});

test('removing one warning does not permit a new deprecated dependency in another file', () => {
    assert.deepEqual(increased({old: 1}, {old: 2}), []);
    assert.deepEqual(increased({other: 1}, {old: 1}), [['other', 1]]);
    assert.deepEqual(increased({old: 2}, {old: 1}), [['old', 2]]);
});
