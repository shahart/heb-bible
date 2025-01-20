import gzip
import requests

class HebBible:
        def __init__(self):
                self.repo = []
                                
                # TODO read from remote:
                # response = requests.get('https://raw.githubusercontent.com/shahart/heb-bible/master/bible.txt.gz') 
                # if response.status_code == 200: 
                        # file_content = response.text 

                with gzip.open('/repos/heb-bible/bible.txt.gz') as f:
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
