import random

countries = [
  "Balinese",
  "Bolivian",
  "Brazilian",
  "Costa Rican",
  "Dominican",
  "Salvadorean",
  "Ethiopian",
  "Guatemalan",
  "Indian",
  "Kenyan",
  "Malian",
  "Mexican",
  "Panamanian",
  "Peruvian",
  "Sumatran"]

adjectives = [
  "Supremo",
  "Honey Burst",
  "Sidamo",
  "Longberry",
  "Caturra",
  "Reserve",
  "AA",
  "Peaberry",
  "Mandheling",
  "Paradise Valley",
  "Black Satin",
  "Swiss Water",
  "Cuzcachapa"]

FT = ["", "FT"]
organic = ["", "organic"]
decaf = ["", "decaf"]

def randbin():
  return random.randint(0,1)

class Coffee:
  organic = False
  decaf = False
  FT = False
  adjective = ""
  country = ""

  def __init__(self, organic=randbin(),
                     decaf=randbin(),
                     FT=randbin(),
                     country=random.choice(countries),
                     adjective=random.choice(adjectives)):
    self.organic = organic
    self.decaf = decaf
    self.FT = FT
    self.country = country
    self.adjective = adjective

  @property
  def name(self):
    n = []
    if self.organic:
      n.append("Organic")
    if self.FT:
      n.append("Fair Trade")
    if self.decaf:
      n.append("Decaf")
    n.append(self.country)
    n.append(self.adjective)
    return " ".join(n)



class Person:
  id_number = 0
  organic = False
  decaf = False
  FT = False

  def __init__(self,
               id_number,
               organic=None,
               decaf=None,
               FT=None):
    
    if not organic:
      organic = randbin()
    if not decaf:
      decaf = randbin()
    if not FT:
      FT = randbin()
    self.id_number = id_number
    self.organic = organic
    self.decaf = decaf
    self.FT = FT
    

  def rate(self, coffee):
    rating = 1
    if self.organic == coffee.organic:
      rating += 1
    if self.decaf and not coffee.decaf:
      return 1
    if self.decaf and coffee.decaf:
      rating += 1
    if self.FT == coffee.FT:
      rating += 2
    return rating

if __name__ == "__main__":
  people=[]
  coffees=[]
  for i in xrange(10):
    people.append(Person(i))
  for i in range(10):
    coffees.append(Coffee(randbin(), randbin(), randbin(),
               random.choice(adjectives), 
               random.choice(countries)))
  for p in people:
    for i in xrange(len(coffees)/2):
      c = random.choice(coffees)
      print "%s\t%s\t%s" %(p.id_number, c.name, p.rate(c))
