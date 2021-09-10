[![License](https://img.shields.io/badge/License-EPL%202.0-red.svg)](https://www.eclipse.org/legal/epl-2.0/)
# eaSirius
This Eclipse plugin contains model-to-text transformations for generating client-side browser-based graphical modeling editors based on Ecore meta-models and Eclipse Sirius editor descriptions.

## Setup
- TBA

## Usage
- TBA

## Integration of the resulting editor
- generate the bundle with `npm run webpack`
- integrate the generated asset from the folder *dist/* in your application
- define four preferrrably empty `<div></div>` elements with the following IDs:
  - `generatedEditor`
  - `generatedEditorPalette`
  - `generatedEditorLifecycleManagement`
  - `generatedEditorPropertiesPanel`
  
# License
Eclipse Public License - v 2.0, see [LICENSE](../main/LICENSE)
