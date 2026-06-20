package pasuk

import (
	"sync"
	"testing"
)

func TestPasuk_Hebrew(t *testing.T) {
	for _, c := range []struct {
		in           string
		containsName bool
		want         int
	}{
		{"שחר", false, 25},
		{"שחר", true, 75},
	} {
		got := Pasuk(c.in, c.containsName)
		if got != c.want {
			t.Errorf("%q == %d, want %d", c.in, got, c.want)
		}
	}
}

func TestPasuk_Contains(t *testing.T) {
	// Prepare in-memory verses and mark load as done to avoid network fetch.
	verses = []string{"hello world", "say hello", "no match here", "xhelloy"}
	// mark once as done so ensureLoaded won't attempt to fetch
	once.Do(func() {})
	// Sanity: use substring match
	count := Pasuk("hell", true)
	if count != 3 {
		t.Fatalf("expected 3 matches, got %d", count)
	}
}

func TestPasuk_EdgeMatch(t *testing.T) {
	// Prepare simple ASCII verses to exercise the edge-character check
	verses = []string{"xbz", "abz", "ybz", "short"}
	// reset once and mark done
	once = sync.Once{}
	once.Do(func() {})

	// name where name[1]=='b' and last char == 'z'
	count := Pasuk("abz", false)
	if count != 3 {
		t.Fatalf("expected 3 edge matches, got %d", count)
	}
}
