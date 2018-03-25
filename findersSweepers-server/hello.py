from datetime import datetime
from flask import Flask,request
from flask_sqlalchemy import SQLAlchemy
from math import sin, cos, sqrt, atan2, radians


app = Flask(__name__)
app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:////tmp/test.db'
db = SQLAlchemy(app)

names={}

@app.route('/', methods=['GET', 'POST'])

def distFromCoords(lat1,lon1,lat2,lon2):
    R = 6371.0
    lat1 = radians(lat1)
    lon1 = radians(lon1)
    lat2 = radians(lat2)
    lon2 = radians(lon2)

    dlon = lon2 - lon1
    dlat = lat2 - lat1

    a = sin(dlat / 2)**2 + cos(lat1) * cos(lat2) * sin(dlon / 2)**2
    c = 2 * atan2(sqrt(a), sqrt(1 - a))

    distance = R * c
    return distance

def findClose(loc):
    close=[]
    for trash in db:
        if distFromCoords(loc[0],loc[1],trash.loc[0],trash.loc[1])<1:
            close.append(trash)
    return close

def parse_request():
    # data = request.args
    # username = request.args.get("username")
    #f=open('usercount.txt','w')
    #username = int(f.readlines[0])
    location="hello"
    category = "byeeee"
    trash = Trash(location=location,category=category)
    db.session.add(trash)
    db.session.commit()
    
    #f.write(int(f.readlines[0])+1)
    #f.close()
    #User.query.filter_by(username=username)
    
    print(request.args)
    
    return "complete"

#def index():
#    return "Example Index"

class Trash(db.Model): # one piece of trash
    location = db.Column(db.String(120), unique=True, nullable=False,primary_key=True)
    category = db.Column(db.String(120), unique=True, nullable=False,primary_key=True)

    def __repr__(self):
        return '<Trash %r>' % self.location, self.category

##class Post(db.Model):
##    id = db.Column(db.Integer, primary_key=True)
##    user_id = db.Column(db.Integer)
##    title = db.Column(db.String(80), nullable=False)
##    body = db.Column(db.Text, nullable=False)
##    pub_date = db.Column(db.DateTime, nullable=False,
##        default=datetime.utcnow)
##    def __repr__(self):
##        return '<Post %r>' % self.title
##
##
##class Category(db.Model):
##    password = db.Column(db.Integer, primary_key=True, nullable=False)
##    username = db.Column(db.String(50), nullable=False,primary_key=True)
##
##    def __repr__(self):
##        return '<Category %r>' % self.name
    
if __name__ == '__main__':
    app.secret_key = "some secret"
    db.create_all()
    app.run()
