from openai import OpenAI
import os
from dotenv import load_dotenv


load_dotenv()
API_KEY = os.getenv("API_KEY")

with open("lab1-code.cpp", "r", encoding="utf-8") as f:
    code = f.read()

client = OpenAI(api_key=API_KEY, base_url="https://openrouter.ai/api/v1")

try:
    response = client.chat.completions.create(
        model="gpt-oss-20b:free",
        messages=[
            {"role": "system", "content": "You are C++ compiler. You only need to say what will be output of given code. No explanations."},
            {"role": "user", "content": code},
        ]
    )
except:
    print("Error during handling model response")

print(response.choices[0].message.content)
