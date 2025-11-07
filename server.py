from flask import Flask, request
from datetime import datetime, UTC
import os

app = Flask(__name__)
os.makedirs('uploads', exist_ok=True)

@app.post('/upload')
def upload():
    data = request.get_data(as_text=True)
    ts = datetime.now(UTC).strftime('%Y-%m-%d %H:%M:%S.%f')[:-3] 
    path = os.path.join('uploads', f'plain_{ts.replace(":", "").replace(" ", "_")}.txt')

    with open(path, 'w', encoding='utf-8') as f:
        f.write(data)

    # warna biar jelas
    GREEN = "\033[92m"
    CYAN = "\033[96m"
    RESET = "\033[0m"

    print(f"{GREEN}[EXFIL DETECTED]{RESET}")
    print(f"  Time    : {CYAN}{ts}{RESET}")
    print(f"  Bytes   : {len(data)}")
    print(f"  Preview : {data[:80]!r}")
    print(f"  Saved   : {path}")
    print("-" * 60)

    return 'OK', 200

if __name__ == '__main__':
    app.run(host='127.0.0.1', port=5000, threaded=True)
