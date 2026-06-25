export function actionLabel(action: string) {
  return action
    .toLowerCase()
    .split('_')
    .filter(Boolean)
    .join(' ')
    .replace(/^./, (character) => character.toUpperCase());
}
