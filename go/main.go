package main

import (
	"encoding/json"
	"log"
	"net/http"
	"strconv"

	"example.com/pasuk"
)

type response struct {
	Count int `json:"count"`
}

func psukimHandler(w http.ResponseWriter, r *http.Request) {
	name := r.URL.Query().Get("name")
	if name == "" {
		http.Error(w, "missing 'name' query parameter", http.StatusBadRequest)
		return
	}
	containsName := false
	if v := r.URL.Query().Get("containsName"); v != "" {
		b, err := strconv.ParseBool(v)
		if err == nil {
			containsName = b
		}
	}

	count := pasuk.Pasuk(name, containsName)

	w.Header().Set("Content-Type", "application/json")
	if err := json.NewEncoder(w).Encode(response{Count: count}); err != nil {
		log.Println("encode response:", err)
	}
}

func main() {
	http.HandleFunc("/psukim", psukimHandler)
	log.Println("listening on :8080")
	log.Fatal(http.ListenAndServe(":8080", nil))
}
