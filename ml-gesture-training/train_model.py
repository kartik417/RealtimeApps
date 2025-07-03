import pandas as pd
from sklearn.model_selection import train_test_split
from sklearn.ensemble import RandomForestClassifier
import joblib

# Load the merged data
df = pd.read_csv("all_gestures.csv")

# Separate features and labels
X = df.drop("label", axis=1)
y = df["label"]

# Split into training and testing sets
X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)

# Train Random Forest model
model = RandomForestClassifier(n_estimators=100, random_state=42)
model.fit(X_train, y_train)

# Evaluate
accuracy = model.score(X_test, y_test)
print(f"✅ Model trained with {accuracy*100:.2f}% accuracy")

# Save the model
joblib.dump(model, "gesture_model.pkl")
print("💾 Model saved as gesture_model.pkl")
