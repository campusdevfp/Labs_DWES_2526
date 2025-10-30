package app.service;

import app.domain.Mano;
import app.domain.Tenista;
import app.repository.TenistaRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TenistaService {
    private final TenistaRepository repo;

    public TenistaService(TenistaRepository repo) {
        this.repo = repo;
    }

    public List<Tenista> getAllOrderByPuntosDesc() throws Exception {
        return repo.findAllOrderByPuntosDesc();
    }

    public double mediaAltura() throws Exception {
        List<Tenista> list = repo.findAll();
        if (list.isEmpty()) return 0.0;
        long sum = 0;
        for (int i = 0; i < list.size(); i++) sum += list.get(i).getAltura();
        return (double) sum / list.size();
    }

    public double mediaPeso() throws Exception {
        List<Tenista> list = repo.findAll();
        if (list.isEmpty()) return 0.0;
        long sum = 0;
        for (int i = 0; i < list.size(); i++) sum += list.get(i).getPeso();
        return (double) sum / list.size();
    }

    public Tenista tenistaMasAlto() throws Exception {
        List<Tenista> list = repo.findAll();
        Tenista best = null;
        for (int i = 0; i < list.size(); i++) {
            Tenista t = list.get(i);
            if (best == null || t.getAltura() > best.getAltura()) best = t;
        }
        return best;
    }

    public List<Tenista> tenistasPorPais(String pais) throws Exception {
        return repo.findByPais(pais);
    }

    public void printTenistasAgrupadosPorPais() throws Exception {
        List<Tenista> list = repo.findAll();
        Map<String, List<Tenista>> map = new HashMap<>();
        for (int i = 0; i < list.size(); i++) {
            Tenista t = list.get(i);
            List<Tenista> group = map.get(t.getPais());
            if (group == null) {
                group = new ArrayList<>();
                map.put(t.getPais(), group);
            }
            // insert preserving order by puntos desc (list is already sorted)
            group.add(t);
        }
        for (Map.Entry<String, List<Tenista>> e : map.entrySet()) {
            System.out.println("- " + e.getKey());
            List<Tenista> gl = e.getValue();
            for (int i = 0; i < gl.size(); i++) {
                System.out.println("   " + gl.get(i).toShortString());
            }
        }
    }

    public void printNumTenistasPorPaisOrdenadosPorPuntos() throws Exception {
        List<Tenista> list = repo.findAll();
        Map<String, Integer> count = new HashMap<>();
        Map<String, List<Tenista>> map = new HashMap<>();
        for (int i = 0; i < list.size(); i++) {
            Tenista t = list.get(i);
            Integer c = count.get(t.getPais());
            count.put(t.getPais(), c == null ? 1 : c + 1);
            List<Tenista> gl = map.get(t.getPais());
            if (gl == null) {
                gl = new ArrayList<>();
                map.put(t.getPais(), gl);
            }
            gl.add(t); // ya vienen por puntos desc globalmente
        }
        for (Map.Entry<String, Integer> e : count.entrySet()) {
            System.out.println(String.format(Locale.ROOT, "%s -> %d tenistas", e.getKey(), e.getValue()));
            List<Tenista> gl = map.get(e.getKey());
            for (int i = 0; i < gl.size(); i++) {
                System.out.println("   " + gl.get(i).toShortString());
            }
        }
    }

    public void printTenistasPorManoConMedia() throws Exception {
        List<Tenista> list = repo.findAll();
        Map<Mano, Integer> count = new HashMap<>();
        Map<Mano, Long> sum = new HashMap<>();
        for (int i = 0; i < list.size(); i++) {
            Tenista t = list.get(i);
            Mano m = t.getMano();
            count.put(m, (count.get(m) == null ? 0 : count.get(m)) + 1);
            sum.put(m, (sum.get(m) == null ? 0L : sum.get(m)) + t.getPuntos());
        }
        for (Map.Entry<Mano, Integer> e : count.entrySet()) {
            Mano m = e.getKey();
            int c = e.getValue();
            long s = sum.get(m) == null ? 0L : sum.get(m);
            double media = c == 0 ? 0.0 : (double) s / c;
            System.out.println(m + " -> n=" + c + ", media puntos=" + String.format(Locale.ROOT, "%.2f", media));
        }
    }

    public void printPuntuacionTotalPorPais() throws Exception {
        List<Tenista> list = repo.findAll();
        Map<String, Long> sum = new HashMap<>();
        for (int i = 0; i < list.size(); i++) {
            Tenista t = list.get(i);
            String k = t.getPais();
            sum.put(k, (sum.get(k) == null ? 0L : sum.get(k)) + t.getPuntos());
        }
        for (Map.Entry<String, Long> e : sum.entrySet()) {
            System.out.println(e.getKey() + " -> total=" + e.getValue());
        }
    }

    public String paisConMasPuntuacionTotal() throws Exception {
        List<Tenista> list = repo.findAll();
        Map<String, Long> sum = new HashMap<>();
        for (int i = 0; i < list.size(); i++) {
            Tenista t = list.get(i);
            String k = t.getPais();
            sum.put(k, (sum.get(k) == null ? 0L : sum.get(k)) + t.getPuntos());
        }
        String bestPais = null;
        long best = Long.MIN_VALUE;
        for (Map.Entry<String, Long> e : sum.entrySet()) {
            if (e.getValue() > best) {
                best = e.getValue();
                bestPais = e.getKey();
            }
        }
        return bestPais == null ? "N/A" : bestPais + " (" + best + ")";
    }

    public Tenista mejorRankingDePais(String pais) throws Exception {
        List<Tenista> list = repo.findByPais(pais);
        if (list.isEmpty()) return null;
        return list.get(0); // ya ordenado por puntos desc
    }
}
