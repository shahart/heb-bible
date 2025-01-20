import pytest
from app import app

@pytest.fixture
def client():
    app.config['TESTING'] = True
    with app.test_client() as client:
        yield client

def test_total_psukim(client):
    response = client.get("/psukim")
    assert response.status_code == 200
    assert b"23204" in response.data


def test_psukim_by_name(client):
    response = client.get("/psukim/שחר")
    assert response.status_code == 200
    assert b"25" in response.data
