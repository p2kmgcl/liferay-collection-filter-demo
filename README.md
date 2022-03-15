# Enhance your Collection Display Fragments with Custom Filters

This repo contains the sample code used in the "Enhance your Collection Display Fragments with Custom Filters" talk for [Liferay Devcon 2022](https://www.liferay.com/es/web/events/devcon2022).

Specifically, it contains an `InfoCollectionProvider` that retrieves the list of the basic documents from a site. This Collection Provider also implements the `FilteredInfoCollectionProvider` interface, which allows to filter the list of documents, in this case by categories or by the created date. 

## Steps to run this project

This repo is configured to work with the Liferay 7.4 GA15. You can download it [here](https://github.com/liferay/liferay-portal/releases/tag/7.4.3.15-ga15). 

> The module included in this repo can work with any version of the Liferay Portal 7.4, but you will need to tweak [build.gradle](https://github.com/p2kmgcl/liferay-collection-filter-demo/blob/master/modules/date-filter/build.gradle) file with the corresponding versions.

After having a local copy of Liferay Portal, you have to add the path to the portal in the [gradle.properties](https://github.com/p2kmgcl/liferay-collection-filter-demo/blob/master/gradle.properties) file.

Then you'd only have to execute:

```bash
./gradlew deploy
```

After that you can use the included "Basic Document" collection provider in any page and the date filter with it.


- [ ] Document which version of portal should being used
- [ ] Link to the slides (and the video when it is ready)
- [ ] Write a brief explanation of what is going on here
