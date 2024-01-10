package net.deneo.adup.utility;

import net.deneo.adup.Adup;

import javax.net.ssl.HttpsURLConnection;
import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

public class DiscordUtil {
    private final String url;
    private final List<EmbedObject> embeds = new ArrayList<>();
    private String username;
    private String avatarUrl;

    public DiscordUtil(String url) {
        this.url = url;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public void addEmbed(EmbedObject embed) {
        this.embeds.add(embed);
    }

    public void execute() {
        if (this.embeds.isEmpty()) {
            throw new IllegalArgumentException("Add at least one EmbedObject");
        }

        JSONObject json = new JSONObject();
        json.put("username", this.username);
        json.put("avatar_url", this.avatarUrl);
        if (!this.embeds.isEmpty()) {
            List<JSONObject> embedObjects = new ArrayList<>();
            for (EmbedObject embed : this.embeds) {
                JSONObject jsonEmbed = new JSONObject();
                jsonEmbed.put("title", "");
                jsonEmbed.put("description", "");
                jsonEmbed.put("url", "");
                if (embed.getColor() != null) {
                    Color color = embed.getColor();
                    int rgb = color.getRed();
                    rgb = (rgb << 8) + color.getGreen();
                    rgb = (rgb << 8) + color.getBlue();
                    jsonEmbed.put("color", rgb);
                }

                EmbedObject.Footer footer = embed.getFooter();
                List<EmbedObject.Field> fields = embed.getFields();
                JSONObject jsonAuthor;
                if (footer != null) {
                    jsonAuthor = new JSONObject();
                    jsonAuthor.put("text", footer.getText());
                    jsonAuthor.put("icon_url", footer.getIconUrl());
                    jsonEmbed.put("footer", jsonAuthor);
                }

                List<JSONObject> jsonFields = new ArrayList<>();
                for (EmbedObject.Field field : fields) {
                    JSONObject jsonField = new JSONObject();
                    jsonField.put("name", field.getName());
                    jsonField.put("value", field.getValue());
                    jsonField.put("inline", field.isInline());
                    jsonFields.add(jsonField);
                }

                jsonEmbed.put("fields", jsonFields.toArray());
                embedObjects.add(jsonEmbed);
            }

            json.put("embeds", embedObjects.toArray());
        }

        try {
            URL url = URI.create(this.url).toURL();
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.addRequestProperty("Content-Type", "application/json");
            connection.addRequestProperty("User-Agent", "Java-DiscordWebhook-BY-Gelox_");
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            OutputStream stream = connection.getOutputStream();
            stream.write(json.toString().getBytes());
            stream.flush();
            stream.close();
            connection.getInputStream().close();
            connection.disconnect();
        } catch (IOException ex) {
            Adup.error("Executing embed failed! (DiscordUtil)", ex);
        }
    }

    public static class EmbedObject {
        private final List<EmbedObject.Field> fields = new ArrayList<>();
        private Color color;
        private EmbedObject.Footer footer;

        public Color getColor() {
            return this.color;
        }

        public void setColor(Color color) {
            this.color = color;
        }

        public EmbedObject.Footer getFooter() {
            return this.footer;
        }

        public List<EmbedObject.Field> getFields() {
            return this.fields;
        }

        public void setFooter(String text, String icon) {
            this.footer = new Footer(text, icon);
        }

        public void addField(String name, String value, boolean inline) {
            this.fields.add(new Field(name, value, inline));
        }

        static class Footer {
            private final String text;
            private final String iconUrl;

            private Footer(String text, String iconUrl) {
                this.text = text;
                this.iconUrl = iconUrl;
            }

            private String getText() {
                return this.text;
            }

            private String getIconUrl() {
                return this.iconUrl;
            }
        }

        static class Field {
            private final String name;
            private final String value;
            private final boolean inline;

            private Field(String name, String value, boolean inline) {
                this.name = name;
                this.value = value;
                this.inline = inline;
            }

            private String getName() {
                return this.name;
            }

            private String getValue() {
                return this.value;
            }

            private boolean isInline() {
                return this.inline;
            }
        }
    }

    static class JSONObject {
        private final HashMap<String, Object> map;

        JSONObject() {
            this.map = new HashMap<>();
        }

        void put(String key, Object value) {
            if (value != null) {
                this.map.put(key, value);
            }

        }

        public String toString() {
            StringBuilder builder = new StringBuilder();
            Set<Entry<String, Object>> entrySet = this.map.entrySet();
            builder.append("{");
            int i = 0;
            for (Entry<String, Object> entry : entrySet) {
                Object val = entry.getValue();
                builder.append(this.quote(entry.getKey())).append(":");
                if (val instanceof String) {
                    builder.append(this.quote(String.valueOf(val)));
                } else if (val instanceof Integer) {
                    builder.append(Integer.valueOf(String.valueOf(val)));
                } else if (val instanceof Boolean) {
                    builder.append(val);
                } else if (val instanceof JSONObject) {
                    builder.append(val);
                } else if (val.getClass().isArray()) {
                    builder.append("[");
                    int len = Array.getLength(val);

                    for (int j = 0; j < len; ++j) {
                        builder.append(Array.get(val, j).toString()).append(j != len - 1 ? "," : "");
                    }

                    builder.append("]");
                }

                ++i;
                builder.append(i == entrySet.size() ? "}" : ",");
            }

            return builder.toString();
        }

        private String quote(String string) {
            return "\"" + string + "\"";
        }
    }
}
