import requests

API_URL = "http://127.0.0.1:8083"   # or host.docker.internal
API_KEY = "p7OIuc2lKJr4U6CkqK14R6rjkZYVixMqAuDKR3KtzYFmPO6z5LFocQWqjwdcvobq"    # the API-Key from config.yml (gets generated on first start)

def add_player(name: str):
    r = requests.post(
        f"{API_URL}/whitelist/add",
        headers={"X-API-KEY": API_KEY},
        json={"name": name}
    )
    print(f"Add {name}: {r.status_code} -> {r.text}")

def remove_player(name: str):
    r = requests.post(
        f"{API_URL}/whitelist/remove",
        headers={"X-API-KEY": API_KEY},
        json={"name": name}
    )
    print(f"Remove {name}: {r.status_code} -> {r.text}")

def get_player(name: str):
    r = requests.get(
        f"{API_URL}/whitelist/{name}",
        headers={"X-API-KEY": API_KEY}
    )
    print(f"Get {name}: {r.status_code} -> {r.text}")

if __name__ == "__main__":
    test_name = input("Enter player name to test: ")

    print(f"\nPress enter to add {test_name} to the whitelist.")
    input()

    print(f"--- TEST: Add {test_name} to whitelist. ---")
    add_player(test_name)

    print(f"\n--- TEST: Get {test_name} ---")
    get_player(test_name)

    print(f"\nPress enter to remove {test_name} from the whitelist.")
    input()

    print("\n--- TEST: Remove Player ---")
    remove_player(test_name)

    print("\n--- TEST: Get Player Again ---")
    get_player(test_name)
