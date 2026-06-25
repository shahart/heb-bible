import gzip
from pathlib import Path

class HebBible:
    def __init__(self):
        self.repo = []
        self._tor_txt_parts = []
        self._verse_end = []

        data_file = Path(__file__).resolve().parent.parent / 'bible.txt.gz'

        cum_letters = 1
        with gzip.open(data_file) as f:
            for line in f:
                lin = line.decode('utf8').strip()
                splits = lin.split(",")
                txt = splits[1]
                self.repo.append(txt)

                txt_clean = txt.replace(" ", "")
                txt_norm = self._suffix(txt_clean)
                self._tor_txt_parts.append(txt_norm)
                cum_letters += len(txt_clean)
                self._verse_end.append(cum_letters)

        self._total_letters = cum_letters
        print(len(self.repo), "psukim")

    @staticmethod
    def _suffix(txt):
        txt = txt.replace('ך', 'כ')
        txt = txt.replace('ם', 'מ')
        txt = txt.replace('ן', 'נ')
        txt = txt.replace('ף', 'פ')
        txt = txt.replace('ץ', 'צ')
        return txt

    def total_psukim(self):
        return len(self.repo)

    def psukim_by_name(self, name):
        print('/get', name)
        result = []
        for psk in self.repo:
            if (psk[0] == name[0] and psk[-1] == name[-1]):
                result.append(psk)
        print(result)
        return len(result)

    def dilugim(self, target, skip_min, skip_max):
        target = target.replace(" ", "")
        target = self._suffix(target)
        target_len = len(target)

        tor_txt = "".join(self._tor_txt_parts)

        found = 0
        for i_skip in range(skip_min, skip_max + 1):
            print('skip ', target)
            last_ind = self._total_letters - (target_len - 1) * i_skip
            for j in range(last_ind):
                match = True
                for k in range(target_len):
                    if tor_txt[j + k * i_skip] != target[k]:
                        match = False
                        break

                if not match:
                    match = True
                    for k in range(target_len):
                        if tor_txt[j + k * i_skip] != target[target_len - k - 1]:
                            match = False
                            break

                if match:
                    found += 1
                    print('new dilug of ' + str(i_skip) + ' from ' + str(j))
                    if found >= 50:
                        return found

        return found
