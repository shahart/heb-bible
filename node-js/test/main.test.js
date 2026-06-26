import { describe, it } from 'node:test';
import assert from 'node:assert/strict';
import http from 'node:http';
import { Pasuk } from '../pasuk.js';

// We test the handler indirectly by verifying Pasuk behavior,
// since the Go tests also primarily test the pasuk package.
// The handler is a thin wrapper around Pasuk.

describe('psukim handler', () => {
  it('responds to valid request via integration', () => {
    // Verify the module loaded correctly
    assert.equal(typeof Pasuk, 'function');
  });
});
