<?php
declare(strict_types=1);

require_once __DIR__ . '/../src/BibleLoader.php';
require_once __DIR__ . '/../src/PasukService.php';

use HebBible\BibleLoader;
use HebBible\PasukService;

$checks = 0;
$assertSame = static function (mixed $expected, mixed $actual, string $message) use (&$checks): void {
    $checks++;
    if ($expected !== $actual) {
        throw new RuntimeException("{$message}: expected " . var_export($expected, true)
            . ', got ' . var_export($actual, true));
    }
};

$service = new PasukService(BibleLoader::load());
$assertSame(0, $service->count('', false), 'Empty name');
$assertSame(25, $service->count('שחר', false), 'Hebrew byte-edge matching');
$assertSame(75, $service->count('שחר', true), 'Hebrew substring matching');

$request = static function (string $path, array $query): array {
    $_SERVER['REQUEST_URI'] = $path;
    $_SERVER['REQUEST_METHOD'] = 'GET';
    $_GET = $query;
    http_response_code(200);
    ob_start();
    include __DIR__ . '/../index.php';
    $body = ob_get_clean();
    return [http_response_code(), json_decode($body, true)];
};

[$status, $body] = $request('/psukim', ['name' => 'שחר']);
$assertSame(200, $status, 'API success status');
$assertSame(['count' => 25], $body, 'API JSON response');

[$status, $body] = $request('/psukim', []);
$assertSame(400, $status, 'Missing name status');
$assertSame(["error" => "missing 'name' query parameter"], $body, 'Missing name response');

echo "Passed {$checks} checks.\n";
