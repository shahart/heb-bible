# Counts skip-pattern matches in the normalized Torah text.
class DilugimService
  MAX_MATCHES = 50

  def initialize(verses)
    @torah = verses.map { |verse| normalize(verse).delete(' ') }.join
  end

  def count(target, skip_min, skip_max)
    target = normalize(target.to_s).delete(' ')
    length = target.length
    return 0 if length.zero? || skip_min < 1 || skip_max < skip_min

    found = 0
    (skip_min..skip_max).each do |skip|
      last_start = @torah.length - (length - 1) * skip
      break if last_start <= 0

      (0...last_start).each do |start|
        candidate = length.times.map { |index| @torah[start + index * skip] }.join
        next unless candidate == target || candidate == target.reverse
        puts "skip: #{skip}, start: #{start}"
        found += 1
        return found if found >= MAX_MATCHES
      end
    end
    found
  end

  private

  def normalize(text)
    text.tr('ךםןףץ', 'כמנפצ')
  end
end
