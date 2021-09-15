[![License](https://img.shields.io/badge/License-EPL%202.0-red.svg)](https://www.eclipse.org/legal/epl-2.0/)
# eaSirius
This Eclipse plugin contains model-to-text transformations for generating client-side browser-based graphical modeling editors based on Ecore meta-models and Eclipse Sirius editor descriptions.

## Requirements
- JDK 11
- An Eclipse instance with installed plugins for [EMF](https://www.eclipse.org/modeling/emf/) (developed with release 2.26), [Sirius](https://www.eclipse.org/sirius/) (6.5.1) and [Epsilon](https://www.eclipse.org/epsilon/) (2.3.0)
  - The fastest way to set up such an instance is by installing [Obeo Designer](https://www.obeodesigner.com/en/download) (EPL licensed) and then installing Epsilon via the [Eclipse Marketplace](https://marketplace.eclipse.org/content/epsilon)

## Installation
- Clone the repository via
  - ```git clone --recursive git@github.com:andreasdomanowski/eaSirius.git``` (ssh) or
  - ```git clone --recursive https://github.com/andreasdomanowski/eaSirius.git``` (https)
- Import the two cloned projects in your Eclipse instance
  - ```File``` -> ```Import``` -> ```General``` -> ```Existing Projects into Workspace```
  - Check ```Select Root Directory``` and set the path to the cloned repostitory
  - Select the projects ```net.domanowski.easirius``` and ```com.crossecore.generator.easirius```
  - Click ```Finish```

## Usage
- Right click on ```net.domanowski.easirius```
- Select ```Run As``` -> ```Eclispe Application```
- In the spawned Eclipse instance, click ```eaSirius``` in the upper menu/navigation bar
- Follow the instructions in the wizard. This generates an npm project that any Node/JS/TS compatible IDE is able to import
- To have a look at the generated editor, start the webpack dev server with ```npm run start:dev``` after installing the dependencies via ```npm install``` in the generated folder

## Integration of the resulting editor
- Generate the bundle with `npm run webpack`
- Integrate the generated asset from the folder *dist/* in your application
- Define four preferrrably empty `<div></div>` elements with the following IDs:
  - `generatedEditor`
  - `generatedEditorPalette`
  - `generatedEditorLifecycleManagement`
  - `generatedEditorPropertiesPanel`
  
## License
Eclipse Public License - v 2.0, see [LICENSE](../main/LICENSE)
