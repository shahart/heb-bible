require 'minitest/autorun'
require_relative '../dilugim_service'

class DilugimServiceTest < Minitest::Test
  def test_counts_a_match_at_the_requested_skip
    service = DilugimService.new(['אבגדהוז'])

    assert_equal 1, service.count('אגה', 2, 2)
  end

  def test_counts_matches_in_reverse_order
    service = DilugimService.new(['אבגדהוז'])

    assert_equal 1, service.count('הגא', 2, 2)
  end

  def test_removes_spaces_and_normalizes_final_letters
    service = DilugimService.new(['ךת'])

    assert_equal 1, service.count('כ ת', 1, 1)
  end

  def test_empty_target_and_invalid_skip_ranges_return_zero
    service = DilugimService.new(['אבגדהוז'])

    assert_equal 0, service.count('', 1, 3)
    assert_equal 0, service.count('אב', 0, 3)
    assert_equal 0, service.count('אב', 3, 2)
  end

  def test_caps_match_count_at_fifty
    service = DilugimService.new(['א' * 100])

    assert_equal 50, service.count('א', 1, 1)
  end
end
