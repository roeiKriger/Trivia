from flask import Flask
from Trivia_Dict import quest_and_answers

app = Flask(__name__)
my_str = " you should try: http://1someUrl000/questAndAnswers"


@app.route("/questAndAnswers")
def trivia_questions_and_answers():
    return quest_and_answers


@app.route("/")
def home_page():
    return my_str


if __name__ == "__main__":
    app.run(host="0.0.0.0")
