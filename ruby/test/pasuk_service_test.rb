require 'minitest/autorun'
require_relative '../pasuk_service'

class PasukServiceTest < Minitest::Test
  def setup
    @service = PasukService.new([
      'alpha target omega',
      'cat',
      'bat',
      'dog'
    ])
  end

  def test_empty_name_returns_zero
    assert_equal 0, @service.count('', false)
  end

  def test_contains_name_counts_substring_matches
    count = nil
    service = PasukService.new(['alpha target omega'])
    capture_io { count = service.count('target', true) }
    assert_equal 1, count
  end

  def test_fallback_matches_second_and_last_bytes
    count = nil
    capture_io { count = @service.count('cat', false) }
    assert_equal 2, count
  end

  def test_substring_match_is_not_counted_twice
    count = nil
    service = PasukService.new(['cat'])
    capture_io { count = service.count('cat', true) }
    assert_equal 1, count
  end
end
