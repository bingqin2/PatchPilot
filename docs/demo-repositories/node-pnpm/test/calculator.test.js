import test from 'node:test';
import assert from 'node:assert/strict';
import { add } from '../src/calculator.js';

test('adds numbers', () => {
  assert.equal(add(1, 2), 3);
});
