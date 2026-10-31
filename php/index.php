<?php
declare(strict_types=1);

require_once __DIR__ . '/src/BibleLoader.php';
require_once __DIR__ . '/src/PasukService.php';

use HebBible\BibleLoader;
use HebBible\PasukService;

header('Content-Type: application/json; charset=utf-8');

if (parse_url($_SERVER['REQUEST_URI'] ?? '/', PHP_URL_PATH) !== '/psukim') {
    http_response_code(404);
    echo json_encode(['error' => 'Not Found'], JSON_UNESCAPED_UNICODE);
    return;
}

if (($_SERVER['REQUEST_METHOD'] ?? 'GET') !== 'GET') {
    header('Allow: GET');
    http_response_code(405);
    echo json_encode(['error' => 'Method Not Allowed']);
    return;
}

$name = $_GET['name'] ?? '';
if (!is_string($name) || $name === '') {
    http_response_code(400);
    echo json_encode(['error' => "missing 'name' query parameter"]);
    return;
}

$containsName = ($_GET['containsName'] ?? '') === 'true';

try {
    $service = new PasukService(BibleLoader::load());
    echo json_encode(['count' => $service->count($name, $containsName)], JSON_UNESCAPED_UNICODE);
} catch (Throwable $error) {
    error_log($error->getMessage());
    http_response_code(500);
    echo json_encode(['error' => 'Unable to load Bible text']);
}
