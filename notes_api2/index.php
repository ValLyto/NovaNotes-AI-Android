<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>Notes App</title>
    <style>
        body { background:#f5f7fb; font-family: sans-serif; }
        .container-box {
            max-width: 700px;
            margin: 40px auto;
            background: #fff;
            padding: 25px;
            border-radius: 12px;
            box-shadow: 0 4px 10px rgba(0,0,0,0.1);
        }
        .note-card {
            padding: 12px;
            margin-bottom: 10px;
            border-radius: 8px;
            background: #f1f3f6;
        }
        .form-control { width: 100%; padding: 10px; margin-bottom: 10px; box-sizing: border-box; }
        .btn { padding: 10px; background: #007bff; color: white; border: none; border-radius: 5px; cursor: pointer; width: 100%; }
    </style>
</head>
<body>

<div class="container-box">
    <h2 style="text-align: center; margin-bottom: 20px;">📝 Notes App</h2>

    <form method="POST" style="margin-bottom: 20px;">
        <div>
            <textarea name="note" class="form-control" placeholder="Write your note..." required></textarea>
        </div>
        <button type="submit" class="btn">Save Note</button>
    </form>

    <hr>
    <h4 style="margin-bottom: 15px;">📋 All Notes</h4>

    <?php
    // SAVE NOTE
    if ($_SERVER['REQUEST_METHOD'] == 'POST') {
        $note = $_POST['note'];
        
        
        $url = "http://localhost/notes_api2/add_note.php"; 
        $data = ["content" => $note];
        $options = [
            "http" => [
                "header"  => "Content-type: application/x-www-form-urlencoded",
                "method"  => "POST",
                "content" => http_build_query($data),
            ],
        ];
        $context  = stream_context_create($options);
        file_get_contents($url, false, $context);
    }

    // GET NOTES 
    $response = @file_get_contents("http://localhost/notes_api2/get_notes.php");
    
    if ($response) {
        $notes = json_decode($response, true);
        if ($notes && count($notes) > 0) {
            foreach ($notes as $n) {
                echo "<div class='note-card'>";
                echo "<p style='margin: 0 0 5px 0;'>" . htmlspecialchars($n['content']) . "</p>";
                echo "<small style='color: gray;'>ID: " . $n['id'] . " | Date: " . $n['created_at'] . "</small>";
                echo "</div>";
            }
        } else {
            echo "<p>No notes yet.</p>";
        }
    } else {
        echo "<p>Error connecting to API.</p>";
    }
    ?>
</div>
</body>
</html>