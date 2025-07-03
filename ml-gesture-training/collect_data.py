# collect_data.py
import cv2
import mediapipe as mp
import pandas as pd
import os

mp_hands = mp.solutions.hands
hands = mp_hands.Hands(static_image_mode=False, max_num_hands=1)
mp_draw = mp.solutions.drawing_utils

labels = []         # gesture label list
all_data = []       # landmarks list

cap = cv2.VideoCapture(0)

gesture_name = input("Enter gesture label (e.g. Hello, ThankYou): ")

print("Starting capture. Press 'q' to stop.")

while True:
    ret, frame = cap.read()
    if not ret:
        break

    rgb = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
    results = hands.process(rgb)

    if results.multi_hand_landmarks:
        for hand_landmarks in results.multi_hand_landmarks:
            landmarks = []
            for lm in hand_landmarks.landmark:
                landmarks.extend([lm.x, lm.y, lm.z])
            all_data.append(landmarks)
            labels.append(gesture_name)
            mp_draw.draw_landmarks(frame, hand_landmarks, mp_hands.HAND_CONNECTIONS)

    cv2.imshow("Capture", frame)
    if cv2.waitKey(1) & 0xFF == ord('q'):
        break

cap.release()
cv2.destroyAllWindows()

# Save to CSV
df = pd.DataFrame(all_data)
df["label"] = labels
os.makedirs("data", exist_ok=True)
df.to_csv(f"data/{gesture_name}.csv", index=False)
print(f"Saved data to data/{gesture_name}.csv")
