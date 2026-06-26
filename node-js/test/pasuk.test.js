import { describe, it } from 'node:test';
import assert from 'node:assert/strict';
import { Pasuk } from '../pasuk.js';

describe('Pasuk', () => {
  it('empty name returns 0', () => {
    assert.equal(Pasuk('', false), 0);
  });

  it('returns count for Hebrew name without containsName', () => {
    const count = Pasuk('שחר', false);
    assert.equal(count, 25);
  });

  it('returns count for Hebrew name with containsName', () => {
    const count = Pasuk('שחר', true);
    assert.equal(count, 75);
  });
});
