import type { PageLoad } from '../../../../.svelte-kit/types/src/routes';

export const load: PageLoad = async ({ fetch, params }) => {

    const result = await fetch(``, {
        // method: "GET",
        // headers: {
        //     "Access-Control-Allow-Origin": "*",
        // }
    });

    return {
        // result: result
    };
};