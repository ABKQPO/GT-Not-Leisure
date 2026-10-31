const { test } = require('node:test');
const assert = require('node:assert/strict');
const fs = require('node:fs');
const os = require('node:os');
const path = require('node:path');
const { writeGallery } = require('./ui-gallery.cjs');

function workspace(t) {
    const directory = fs.mkdtempSync(path.join(os.tmpdir(), 'gtnl-gui-report-'));
    t.after(() => {
        for (const name of ['frame.png', 'popup.png', 'gallery.html', 'screenshots.json'])
            fs.rmSync(path.join(directory, name), { force: true });
        fs.rmdirSync(directory);
    });
    return directory;
}
const frame = Buffer.from(
    'iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mP8/x8AAwMCAO+a3ioAAAAASUVORK5CYII=',
    'base64',
);
test('keeps a reviewable gallery for failed runs, with escaped details and image hashes', (t) => {
    const directory = workspace(t);
    fs.writeFileSync(path.join(directory, 'frame.png'), frame);
    fs.writeFileSync(path.join(directory, 'popup.png'), frame);
    const manifest = writeGallery(directory, {
        runId: 'r1',
        state: 'FAIL',
        results: [
            {
                id: 'machine.a.gui',
                status: 'PASS',
                screenshot: 'frame.png',
                additionalScreenshots: ['popup.png'],
                checks: ['native-popup'],
            },
            { id: 'machine.b.gui', status: 'FAIL', detail: '<script>"bad" & no frame</script>' },
        ],
    });
    assert.equal(manifest.length, 2);
    assert.equal(manifest[0].width, 1);
    assert.match(manifest[0].sha256, /^[a-f0-9]{64}$/);
    const html = fs.readFileSync(path.join(directory, 'gallery.html'), 'utf8');
    assert.ok(html.includes('machine.b.gui'));
    assert.ok(html.includes('&lt;script&gt;&quot;bad&quot; &amp;'));
    assert.ok(!html.includes('<script>'));
    assert.ok(html.includes('popup.png') && html.includes('native-popup'));
});
test('rejects missing images, paths outside the run, and truncated image headers', (t) => {
    const directory = workspace(t);
    const report = { runId: 'r1', state: 'PASS', results: [{ id: 'machine.a.gui', status: 'PASS' }] };
    assert.throws(() => writeGallery(directory, report), /No screenshot/);
    report.results[0].screenshot = '../outside.png';
    assert.throws(() => writeGallery(directory, report), /escapes/);
    report.results[0].screenshot = 'frame.png';
    fs.writeFileSync(path.join(directory, 'frame.png'), frame.subarray(0, 8));
    assert.throws(() => writeGallery(directory, report), /not PNG/);
    fs.writeFileSync(path.join(directory, 'frame.png'), frame);
    report.results[0].additionalScreenshots = ['frame.png'];
    assert.throws(() => writeGallery(directory, report), /reused/);
});
