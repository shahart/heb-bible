using System.IO.Compression;
using System.Text;
using HebBible.Api;
using Xunit;

namespace HebBible.Api.Tests;

public sealed class HebBibleRepositoryTests
{
    [Fact]
    public void PsukimByNameCountsVersesWithMatchingFirstAndLastCharacters()
    {
        var dataFile = Path.GetTempFileName();

        try
        {
            using (var file = File.Create(dataFile))
            using (var gzip = new GZipStream(file, CompressionMode.Compress))
            using (var writer = new StreamWriter(gzip, new UTF8Encoding(false)))
            {
                writer.WriteLine("1,שחר");
                writer.WriteLine("2,שלום");
                writer.WriteLine("3,אחר");
                writer.WriteLine("4,שחר באמצע");
            }

            var bible = new HebBibleRepository(dataFile);

            Assert.Equal(4, bible.TotalPsukim());
            Assert.Equal(1, bible.PsukimByName("שחר"));
            Assert.Equal(2, bible.PsukimByName("שחר", containsName: true));
            Assert.Equal(1, bible.PsukimByName("ר באמצ", containsName: true));
        }
        finally
        {
            File.Delete(dataFile);
        }
    }
}
