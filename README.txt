Pour lancer le projet en mode DEV :

- s'assurer que la ligne CONTEXT:dev N'EST PAS commenté dans le .env situé dans le dossier src/main/resources
- s'assurer que la ligne CONTEXT:prod EST commenté dans le .env situé dans le dossier src/main/resources
- executer la commande   docker-compose up -d   dans un terminal (avec docker installé/lancé)
- lancer le projet sur intelliJ avec la petite flèche en haut a droite



Pour lancer le projet en mode PROD :

- s'assurer que la ligne CONTEXT:dev EST commenté dans le .env situé dans le dossier src/main/resources
- s'assurer que la ligne CONTEXT:prod N'EST PAS commenté dans le .env situé dans le dossier src/main/resources
- executer la commande   docker-compose -f docker-compose.prod.yml up   dans un terminal (avec docker installé/lancé)
- des erreurs Java vont s'afficher dans la console, c'est normal, il faut juste attendre (~30sec)



Problèmes connus de configuration :

- si on change des valeurs dans le .env, attention car les fichiers Docker ne prennent pas en compte le fichier .env
  il faut donc apporter ces modifications aux fichiers Docker
