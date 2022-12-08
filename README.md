# Visualisateur de trafic réseau

---

## Structure du code

---

### Analyseur de trame

Afin de permettre à notre programme de visualiser correctement les octets qui lui sont transmis, il a fallut mettre en place de quoi analyser les trames. C'est dans cet optique que nous avons du implanter les classes suivantes :

Pour la couche 2 (Ethernet) : `MacAddress`, `EthernetType`, `EthernetHeader`, `EthernetFrame` et `EthernetData`.
L'ensemble de ces classes nous permettent d'obtenir différents objets.


### Visualisateur de trafic réseau

## Bugs connus

---

Voici, ci-dessous, une liste des bugs / dysfonctionnements connus :

- [[bug-1](https://github.com/)] l'affichage des trames dans le visualisateur graphique provoque des duplications de lignes
- [[bug-2]()] 