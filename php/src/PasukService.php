<?php
declare(strict_types=1);

namespace HebBible;

final class PasukService
{
    /** @param list<string> $verses */
    public function __construct(private readonly array $verses)
    {
    }

    public function count(string $name, bool $containsName): int
    {
        if ($name === '') {
            return 0;
        }

        $count = 0;
        $nameBytes = $name;
        $nameLength = mb_strlen($name, 'UTF-16LE') / 2;

        foreach ($this->verses as $verse) {
            if ($containsName && str_contains($verse, $name)) {
                $count++;
                continue;
            }

            if (mb_strlen($verse, 'UTF-16LE') / 2 > 1 && $nameLength > 1) {
                $verseBytes = $verse;
                if (isset($verseBytes[1], $nameBytes[1])
                    && $verseBytes[1] === $nameBytes[1]
                    && substr($verseBytes, -1) === substr($nameBytes, -1)) {
                    $count++;
                }
            }
        }

        return $count;
    }
}
