# Counts Bible verses using the same matching rules as the TypeScript service.
class PasukService
  def initialize(verses)
    @verses = verses
  end

  def count(name, contains_name)
    return 0 if name.nil? || name.empty?

    count = 0
    @verses.each do |text|
      if contains_name && text.include?(name)
        puts text
        count += 1
        next
      end

      if utf16_length(text) > 1 && utf16_length(name) > 1 &&
         text.bytes[1] == name.bytes[1] && text.bytes.last == name.bytes.last
        puts text
        count += 1
      end
    end

    count
  end

  private

  def utf16_length(text)
    text.encode(Encoding::UTF_16LE).bytesize / 2
  end
end
