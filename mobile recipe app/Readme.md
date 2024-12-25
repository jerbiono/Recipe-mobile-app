# Projet MOBG5

Ce dépôt contient les sources du projet <Recettes de Cuisine>.

## Description

L'application permet aux utilisateurs de s'inscrire s'authentifier et se déconnecté et quand il est authentifié il peut découvrir les recettes de cuisine. Les fonctionnalités principales incluent la visualisations de recettes en fonction des ingrédients, des types de cuisine, etc. Les utilisateurs peuvent également sauvegarder leurs recettes préférées et suivre des instructions détaillées pour préparer des plats délicieux.

## Persistance des données

L'application conserve un historique des recettes aimé par l'utilisateur. Cet historique est persisté dans la base de données locale de l'application via firebase <https://console.firebase.google.com/u/0/project/mobproject-fb40b/firestore/databases/-default-/data/~2Fusers~2F1QglYdlcueYHyW9nKl4gdkKn7vb2>

## Backend

L'application mobile appelle via des services web le backend <Recettes de Cuisine>. Les sources sont disponibles sur le dépôt <[url du dépôt du backend](https://git.esi-bru.be/2023-20241/mobg5/projets/g54685)>.

## Service rest

Pour collecter les données relatives aux recettes, des appels au service REST suivant sont effectués : Edamam propose une API de recherche de recettes  : https://www.themealdb.com/api.php
pour afficher les recette j'ai utilisé explicitement celle  là :
https://www.themealdb.com/api/json/v1/1/categories.php



## Auteur

**Hadj youssef Nader** g54685