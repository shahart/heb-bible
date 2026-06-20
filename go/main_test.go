package main

import (
	"encoding/json"
	"net/http"
	"net/http/httptest"
	"testing"
)

func TestPsukimHandler_MissingName(t *testing.T) {
	req := httptest.NewRequest(http.MethodGet, "/psukim", nil)
	r := httptest.NewRecorder()

	psukimHandler(r, req)

	if r.Code != http.StatusBadRequest {
		t.Fatalf("expected status 400, got %d", r.Code)
	}
}

func TestPsukimHandler_ValidReturnsJSON(t *testing.T) {
	req := httptest.NewRequest(http.MethodGet, "/psukim?name=שחר", nil)
	r := httptest.NewRecorder()

	psukimHandler(r, req)

	if r.Code != http.StatusOK {
		t.Fatalf("expected status 200, got %d", r.Code)
	}

	if ct := r.Header().Get("Content-Type"); ct != "application/json" {
		t.Fatalf("expected content-type application/json, got %q", ct)
	}

	var resp struct {
		Count int `json:"count"`
	}
	if err := json.NewDecoder(r.Body).Decode(&resp); err != nil {
		t.Fatalf("decode response: %v", err)
	}
	if resp.Count < 25 {
		t.Fatalf("unexpected negative count: %d", resp.Count)
	}
}
