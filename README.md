# 🤟 Real-Time Sign Language Translator (ASL) using Android + Flask + ML

This project enables real-time recognition of American Sign Language (ASL) gestures using an Android app integrated with a Python backend. It combines **MediaPipe** for landmark detection, **custom-trained ML models** for gesture prediction, and a **Flask API server** to process predictions.

---

## 🚀 Features

- 📱 Android app for capturing real-time hand gestures
- 🖐️ Gesture data collection using MediaPipe and OpenCV
- 🧠 Trained ML model (RandomForestClassifier) to classify hand landmarks
- 🔌 Flask backend for prediction via REST API
- 🔁 Live communication between Android and server over WiFi/USB
- 🎯 Custom gesture training support (e.g., A, B, C, D...)

---

## 📁 Project Structure

SignLanguageTranslationActivity/
├── ml-gesture-training/ # Python code for data collection & model training
│ ├── collect_data.py # Collect hand landmarks & export CSV
│ ├── train_model.py # Train gesture model using sklearn
│ └── gesture_model.pkl # Saved ML model
│
│  
│
└── Android App # Real-time gesture capture and API integration


---

## 🧪 How It Works

1. **Data Collection**: 
   - Run `collect_data.py` to record landmarks for each gesture.
   - Save all data to `all_gestures.csv`.

2. **Model Training**: 
   - Run `train_model.py` to train the model and save as `gesture_model.pkl`.

3. **Android App**:
   - Connect Android app to the server using the same WiFi or USB debug mode.
   - The app sends landmarks to `http://<your-pc-ip>:5000/predict`.

---

## ✅ Current Supported Gestures

['a', 'b', 'c', 'd'] # Update this as your model evolves


---

## ⚙️ Setup Instructions

### Backend

```bash
# Clone repo and navigate
cd mlgestureserver
python -m venv env
env\Scripts\activate  # (use source env/bin/activate for Linux/Mac)

# Install dependencies
pip install -r requirements.txt

cd ml-gesture-training
python collect_data.py
python train_model.py

🛠️ Tech Stack
Android (Kotlin + Jetpack Compose/XML)

Python 3.10+

MediaPipe

scikit-learn

Flask

OpenCV

NumPy

🧠 Future Enhancements
Add support for full ASL alphabet and custom words

Integrate text-to-speech for gesture meaning

Improve model using deep learning (e.g., TensorFlow or PyTorch)

Host backend on cloud for global access

🤝 Contribution
Feel free to raise issues or contribute to expanding the gesture dataset and model accuracy.

📸 Demo
Coming soon! (You can embed video/gifs/images here)

🧑‍💻 Author
Kartik Sharma
B.Tech CSE | Android & ML Enthusiast






