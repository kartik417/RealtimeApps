import pandas as pd
import os

data_path = "data"
all_files = [f for f in os.listdir(data_path) if f.endswith(".csv")]

df_list = []
for file in all_files:
    df = pd.read_csv(os.path.join(data_path, file))
    df_list.append(df)

merged_df = pd.concat(df_list, ignore_index=True)
merged_df.to_csv("all_gestures.csv", index=False)
print("✅ Merged CSV saved as all_gestures.csv")
