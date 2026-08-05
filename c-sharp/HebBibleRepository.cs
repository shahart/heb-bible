using System.IO.Compression;

namespace HebBible.Api;

public sealed class HebBibleRepository
{
    private readonly string[] _verses;

    public HebBibleRepository()
        : this(Path.Combine(AppContext.BaseDirectory, "bible.txt.gz"))
    {
    }

    internal HebBibleRepository(string dataFile)
    {
        using var file = File.OpenRead(dataFile);
        using var gzip = new GZipStream(file, CompressionMode.Decompress);
        using var reader = new StreamReader(gzip);

        var verses = new List<string>();
        while (reader.ReadLine() is { } line)
        {
            var separator = line.IndexOf(',');
            if (separator < 0)
            {
                throw new InvalidDataException("A Bible data line does not contain a comma.");
            }

            verses.Add(line[(separator + 1)..].Trim());
        }

        _verses = verses.ToArray();
    }

    public int PsukimByName(string name)
    {
        ArgumentException.ThrowIfNullOrEmpty(name);

        var first = name[0];
        var last = name[^1];

        return _verses.Count(verse =>
            verse.Length > 0 && verse[0] == first && verse[^1] == last);
    }
}
