import { getAccesToken } from "../utils/authUtil";

async function requester(method, url, data) {
  const options = {};

  const accessToken = getAccesToken();

  if (accessToken) {
    options.headers = {
      ...options.headers,
      Authorization: `Bearer ${accessToken}`,
    };
  }

  if (method !== "GET") {
    options.method = method;
  }

  if (data && method !== "GET") {
    if (data instanceof FormData) {
      options.body = data;
    } else {
      options.headers = {
        ...options.headers,
        "Content-Type": "application/json",
      };

      options.body = JSON.stringify(data);
    }
  }

  try {
    const response = await fetch(url, options);
    if (response.status === 204) {
      return;
    }
    const result = await response.json();

    if (!response.ok) {
      throw result || new Error("Something go wrong");
    }
    return result;
  } catch (error) {
    throw error;
  }
}

export const get = requester.bind(null, "GET");
export const post = requester.bind(null, "POST");
export const put = requester.bind(null, "PUT");
export const del = requester.bind(null, "DELETE");

export default { get, post, put, del };
