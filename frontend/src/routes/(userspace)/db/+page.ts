import type {PageLoad} from "../../../../.svelte-kit/types/src/routes/$types";
import {PUBLIC_BACKEND_2} from "$env/static/public";

export const load: PageLoad = async (event) => {

    const res1 = await event.fetch(`/api/member`, {method: "GET"})

    if(res1.ok){
        const res1json = await res1.json();
        return { members: res1json };
    } else {
        return { error : true };
    }
};