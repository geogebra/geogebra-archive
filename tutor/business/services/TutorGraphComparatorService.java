package tutor.business.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.BeanComparator;

import tutor.model.TutorGraph;
import tutor.model.TutorGraphResult;
import tutor.model.TutorVertex;

class TutorGraphComparatorService {
	
	TutorGraph strategyGraph;
	TutorGraph actualGraph;
	TutorGraphResult graphResult;

	protected TutorGraphComparatorService(TutorGraph strategyGraph, TutorGraph actualGraph) {
		this.strategyGraph = strategyGraph;
		this.actualGraph = actualGraph;
		this.graphResult = new TutorGraphResult();
	}
	
	public TutorGraphResult compareGraphs() {
		Map candidates = getCandidates(strategyGraph, actualGraph);
		graphResult.setCandidates(candidates);
		
		//TODO: a partir fels candidats, calcular un numeret.
		return graphResult;
	}
	
	/**
	 * 
	 * @param strategy_graph
	 * @param actual_graph
	 * @return
	 */
	private Map getCandidates(TutorGraph strategy_graph, TutorGraph actual_graph) {
		Set strategy_vertices = strategy_graph.getTutorVertices();
		Arrays.sort(strategy_vertices.toArray(), new BeanComparator("numAdjacentVertices"));
		
		Set actual_vertices = actual_graph.getTutorVertices();
		Arrays.sort(actual_vertices.toArray(), new BeanComparator("numAdjacentVertices"));
		
		// numero de vertices total (size)
		// i de cada un mirar wol numeri de adjacets
		// agafar el 1er de la actual, mirar el numero de adjacents, i buscar
		
		// candidates list per vertex, p.e. el A de actual te candidats el C, D, G d'una estrategia
		// key=vertex, value=list
		Map candidates = new HashMap();
		
		Iterator actual_it = actual_vertices.iterator();
		while (actual_it.hasNext()) {
			TutorVertex actualVertex = (TutorVertex) actual_it.next();
			int actual_num_adj_vertex = actualVertex.getNumAdjacentVertices();
			
			// quants vertex del graf esrategia tenten aquest num de verex adjacents? agafar els seus labels, seran els candidaddts
			// vertices candidates for this vertex
			List strategy_candidates  = getCandidatesList(strategy_graph.getTutorVertices(), actual_num_adj_vertex);
			
			// vertex having candidates
			String actual_label = actualVertex.getLabel();

			candidates.put(actual_label, strategy_candidates);
		}
		
		return candidates;
	}

	private List getCandidatesList(Set vertices, int actual_num_adj_vertex) {
		List candidates_list = new ArrayList();
		Iterator iterator = vertices.iterator();
		while(iterator.hasNext()) {
			TutorVertex vertex = (TutorVertex) iterator.next();
			if( actual_num_adj_vertex == vertex.getNumAdjacentVertices()) {
				candidates_list.add(vertex.getLabel());
			}
		}
		return candidates_list;
	}
}