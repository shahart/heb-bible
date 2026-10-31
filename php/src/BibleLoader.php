<?php
declare(strict_types=1);

namespace HebBible;

final class BibleLoader
{
    /** @var list<string>|null */
    private static ?array $verses = null;

    /** @return list<string> */
    public static function load(): array
    {
        if (self::$verses !== null) {
            return self::$verses;
        }

        $path = getenv('BIBLE_GZIP_PATH') ?: dirname(__DIR__) . '/data/bible.txt.gz';
        $compressed = @file_get_contents($path);
        if ($compressed === false) {
            throw new \RuntimeException("Unable to read compressed Bible text at {$path}");
        }

        $text = gzdecode($compressed);
        if ($text === false) {
            throw new \RuntimeException('Unable to decompress Bible text');
        }

        $verses = [];
        foreach (preg_split('/\r?\n/', $text) ?: [] as $line) {
            $line = trim($line);
            if ($line === '') {
                continue;
            }
            $comma = strpos($line, ',');
            if ($comma !== false) {
                $verses[] = substr($line, $comma + 1);
            }
        }

        error_log('BibleLoader loaded ' . count($verses) . ' verses');
        return self::$verses = $verses;
    }
}
