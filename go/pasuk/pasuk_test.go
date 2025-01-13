package pasuk_test

import (
	"example.com/pasuk"
	"testing"
)

func TestPasuk(t *testing.T) {
	for _, c := range []struct {
		in           string
		containsName bool
		want         int
	}{
		{"שחר", false, 25},
		{"שחר", true, 75},
	} {
		got := pasuk.Pasuk(c.in, c.containsName)
		if got != c.want {
			t.Errorf("%q == %d, want %d", c.in, got, c.want)
		}
	}
}
