import pytest
from fastapi.testclient import TestClient
from app import app

@pytest.fixture
def client():
    with TestClient(app) as client:
        yield client

def test_total_psukim(client):
    response = client.get("/psukim")
    assert response.status_code == 200
    assert response.json() == 23204


def test_psukim_by_name(client):
    response = client.get("/psukim/שחר")
    assert response.status_code == 200
    assert response.json() == 25
