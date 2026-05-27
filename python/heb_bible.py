import gzip
from pathlib import Path

class HebBible:
        def __init__(self):
                self.repo = []

                data_file = Path(__file__).resolve().parent.parent / 'bible.txt.gz'

                with gzip.open(data_file) as f:
                        for line in f:
                                lin = line.decode('utf8').strip()
                                self.repo.append(lin.split(",")[1])

                print(len(self.repo), "psukim")

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
