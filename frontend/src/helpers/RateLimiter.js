/* eslint no-await-in-loop: 0 */
/* eslint no-restricted-syntax: 0 */

/*
* maps a function over args and adds a delay in between function calls
* func: funstion to runWithDelay
* args: list of args to pass to func
* delay: amount of ms to wait in between function calls
* finish: function called after all function calls to func have finished
*/
async function runWithDelay(func, args, delay, finish) {
  for (const arg of args) {
    await new Promise((resolve) => setTimeout(async () => {
      await func(arg);
      resolve();
    }, delay));
  }
  if (finish) {
    finish();
  }
}

export default runWithDelay;
